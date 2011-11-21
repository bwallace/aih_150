package ml.sample;

import java.io.BufferedReader;
import java.io.FileReader;

//import nlp.extraction.NLP_InstanceGenerator;

import shared.Copyable;

import ml.extraction.Generator;

public abstract class Parser <T extends Copyable<T>> {

	protected BufferedReader reader;
	protected Generator<T> generator;

	public Parser(Generator<T> generator) {
		this.generator = generator;
	}
	
	// TODO - fix this total hack
	/*
	public Parser(NLP_InstanceGenerator generator) {
		this.generator = (Generator<T>) generator;
	}
	*/
	
	public void open(String datafile) {
		try {
			reader = new BufferedReader(new FileReader(datafile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// returns null when done
	//public abstract Event<T> next();
	public abstract T next();
}
