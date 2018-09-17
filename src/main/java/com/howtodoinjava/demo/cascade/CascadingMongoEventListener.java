package com.howtodoinjava.demo.cascade;

import com.howtodoinjava.demo.cascade.CascadeSave;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import com.howtodoinjava.demo.sequence.SequenceGenerateUtil;
import com.howtodoinjava.demo.model.Users;
import com.howtodoinjava.demo.model.Role;

import java.util.List;
import java.util.ArrayList;

@Component
public class CascadingMongoEventListener extends AbstractMongoEventListener<Object> {
	
	@Value("${sequence.key}")
	private String sequenceKey;
   
    @Autowired
    private MongoOperations mongoOperations;
    
    @Autowired
	private SequenceGenerateUtil seqUtil;
	
	private List<Object> document = new ArrayList<Object>();

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
		
		Object source = event.getSource();
		
		/*if((source instanceof Users) && (((Users) source).getRoles() != null) ) {
			List<Role> roles = ((Users) source).getRoles();
			for(Role role : roles) {
				role.setId(seqUtil.getNextSequenceId(sequenceKey));
			}
		}*/
		
        ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {

            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				
                ReflectionUtils.makeAccessible(field);
                
                if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
                    final Object fieldValue = field.get(source);
                    if (fieldValue == null) {
                        return;
                    }
                    if(fieldValue instanceof List<?>) {
						for(Object item : (List<?>) fieldValue) {
							persistField(item);
						}
					} else {
						persistField(fieldValue);
					}
					mongoOperations.insertAll(document); // save multiple child object using cascade save operation           
                }
            }
        });
    }
    
    private void persistField(Object fieldValue) {
		
		DbRefFieldCallback callback = new DbRefFieldCallback();
        ReflectionUtils.doWithFields(fieldValue.getClass(), callback);
        if (!callback.isIdFound()) {
			throw new MappingException("Cannot perform cascade save on child object without id set");
        }
        document.add(fieldValue);
        //mongoOperations.save(fieldValue);
	}

    private static class DbRefFieldCallback implements ReflectionUtils.FieldCallback {
		
        private boolean idFound;

        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(field);

            if (field.isAnnotationPresent(Id.class)) {
                idFound = true;
            }
        }

        public boolean isIdFound() {
            return idFound;
        }
    }
}
