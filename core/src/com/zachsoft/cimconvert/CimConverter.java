package com.zachsoft.cimconvert;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import jdk.internal.jline.internal.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CimConverter extends ApplicationAdapter {
	private List<Path> paths;

	private Pixmap pixmap;

    public CimConverter(List<Path> paths) {
		this.paths = paths;
	}

	@Override
	public void create() {
		System.out.println("Created.");

		for (Path path : paths) {
		    if (!Files.exists(path)) {
				System.err.printf("Input file '%s' not found; skipping.\n", path);
				continue;
			}

		    Path outputPath;
		    if (path.toString().endsWith(".cim")) {
				outputPath = Paths.get(path.toString().replaceAll("\\.cim$", ".png"));
				convertFromCim(path, outputPath);
			} else if (path.toString().endsWith(".png")) {
				outputPath = Paths.get(path.toString().replaceAll("\\.png$", ".cim"));
				convertToCim(path, outputPath);
			} else {
				System.err.printf("Input file '%s' is not a supported file type; skipping.\n", path);
				continue;
			}

			System.out.printf("Converted '%s' to '%s'\n", path, outputPath);
		}

		System.exit(0);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void dispose() {
	    pixmap.dispose();
	}

	private void convertFromCim(Path cimPath, Path outputPath) {
		FileHandle handle = new FileHandle(cimPath.toString());
		pixmap = PixmapIO.readCIM(handle);

		FileHandle output = new FileHandle(outputPath.toString());
		PixmapIO.writePNG(output, pixmap);
	}

	private void convertToCim(Path imagePath, Path outputPath) {
    	FileHandle handle = new FileHandle(imagePath.toString());
    	pixmap = new Pixmap(handle);

    	FileHandle output = new FileHandle(outputPath.toString());
    	PixmapIO.writeCIM(output, pixmap);
	}

	public enum ConvertDirection {
    	TO_CIM, FROM_CIM
	}
}
