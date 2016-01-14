package loader;

import exceptions.LoaderException;

public interface Loader extends Runnable {
	void setup(String filepath);
}