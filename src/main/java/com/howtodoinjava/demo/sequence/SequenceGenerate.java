package com.howtodoinjava.demo.sequence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
	Mandatory to create collection and insert code below
	db.createCollection("sequence")
	db.sequence.insert({id: "sequenceId",sequenceNum: 0})
 */
@Document(collection = "sequence")
public class SequenceGenerate {
	
	@Id
	private String sequenceKey;
	private Integer sequenceNum;

	public Integer getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public String getSequenceKey() {
		return sequenceKey;
	}

	public void setSequenceKey(String sequenceKey) {
		this.sequenceKey = sequenceKey;
	}

	@Override
	public String toString() {
		return "Sequence Generation [Key=" + sequenceKey + ", Sequence Number=" + sequenceNum + "]";
	}
}
