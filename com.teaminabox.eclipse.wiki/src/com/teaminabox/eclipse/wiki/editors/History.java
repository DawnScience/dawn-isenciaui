package com.teaminabox.eclipse.wiki.editors;

import java.util.ArrayList;

public class History<T> {

	private ArrayList<T>	items;

	private int				location;

	public History() {
		items = new ArrayList<T>();
		location = -1;
	}

	public void add(T object) {
		if (size() > 0 && object.equals(getCurrent())) {
			return;
		}
		location++;
		int numberToRemove = items.size() - location;
		for (int i = 0; i < numberToRemove; i++) {
			items.remove(location);
		}
		items.trimToSize();
		items.add(object);
	}

	public int size() {
		return items.size();
	}

	public int getLocation() {
		return location;
	}

	public T back() {
		location--;
		T object = items.get(location);
		return object;
	}

	public T next() {
		location++;
		return items.get(location);
	}

	public boolean hasPrevious() {
		return location > 0;
	}

	public boolean hasNext() {
		return location < items.size() - 1;
	}

	public T getCurrent() {
		return items.get(location);
	}

	public String toString() {
		return "Location: " + location + ", " + items.toString();
	}

}
