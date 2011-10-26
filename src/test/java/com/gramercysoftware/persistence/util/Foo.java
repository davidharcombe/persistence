package com.gramercysoftware.persistence.util;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "foo")
public class Foo {
	private long id;
	private Bar bar;
	private String baz;

	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Bar getBar() {
		return bar;
	}

	public void setBar(Bar bar) {
		this.bar = bar;
	}

	public String getBaz() {
		return baz;
	}

	public void setBaz(String baz) {
		this.baz = baz;
	}
}
