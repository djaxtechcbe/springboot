package com.howtodoinjava.demo.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.howtodoinjava.demo.sequence.SequenceException;
import com.howtodoinjava.demo.sequence.SequenceGenerate;

@Component
public class SequenceGenerateUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SequenceGenerateUtil.class);

	@Autowired
	private MongoOperations mongoOperation;

	public Integer getNextSequenceId(String key) throws SequenceException {
		
		LOGGER.info("getNextSequenceId Sequence Key, " + key);

		Query query = new Query(Criteria.where("id").is(key));

		Update update = new Update();
		update.inc("sequenceNum", 1);

		FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);

		SequenceGenerate seq = mongoOperation.findAndModify(query, update, options, SequenceGenerate.class);

		if (seq == null) {
			throw new SequenceException("Unable to get sequence id for key : " + key);
		}

		return seq.getSequenceNum();
	}
}
