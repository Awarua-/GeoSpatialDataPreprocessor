package loader;


import exceptions.ErrorCallback;
import model.Point;

import java.io.File;
import java.nio.charset.Charset;

public abstract class FileLoader {

	protected final Charset charset  = Charset.forName("UTF-8");
	protected File file;
	protected boolean finished;
	protected ErrorCallback errorCallback;

	public abstract Point readFileLine();

	public boolean isFinished() {
		return finished;
	}

	public void setErrorCallback(ErrorCallback errorCallback) {
		this.errorCallback = errorCallback;
	}
}