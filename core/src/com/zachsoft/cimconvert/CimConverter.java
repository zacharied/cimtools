package com.zachsoft.cimconvert;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;

public class CimConverter extends ApplicationAdapter {
	private List<Path> paths;
	private ConvertDirection direction;

    public CimConverter(List<Path> paths, ConvertDirection direction) {
		this.paths = paths;
		this.direction = direction;
	}

	@Override
	public void create() {
		for (Path path : paths) {
		    if (!Files.exists(path)) {
				System.err.printf("Input file '%s' not found; skipping.\n", path);
				continue;
			}

		    // Try to determine conversion direction.
		    ConvertDirection fileDirection = this.direction;
		    boolean directionForced = true;
		    if (fileDirection == null) {
		    	// No direction flag provided.
		    	for (ConvertDirection dir : ConvertDirection.values())
		    		if (path.toString().endsWith("." + dir.sourceExtension))
						fileDirection = dir;
				directionForced = false;
			}

			if (fileDirection == null) {
				// Couldn't determine which way to convert.
				System.err.printf("Cannot determine file type for '%s' and no direction flag specified; skipping.\n",
						path);
				continue;
			}

			Path outputPath;
			if (!directionForced) {
			    // We determined the filetype from its extension, so replace that extension with the output filetype's.
				outputPath = Paths.get(path.toString().replaceAll(
						"\\." + fileDirection.sourceExtension, "." + fileDirection.outputExtension));
			} else {
				// The conversion direction is forced, so just slap the new extension on the end.
				outputPath = Paths.get(path.toString() + "." + fileDirection.outputExtension);
			}

			fileDirection.converter.accept(path, outputPath);

			System.out.printf("Converted '%s' to '%s'\n", path, outputPath);
		}

		System.exit(0);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private static void convertFromCim(Path cimPath, Path outputPath) {
		FileHandle handle = new FileHandle(cimPath.toString());
		Pixmap pixmap = PixmapIO.readCIM(handle);

		FileHandle output = new FileHandle(outputPath.toString());
		PixmapIO.writePNG(output, pixmap);
	}

	private static void convertToCim(Path imagePath, Path outputPath) {
    	FileHandle handle = new FileHandle(imagePath.toString());
    	Pixmap pixmap = new Pixmap(handle);

    	FileHandle output = new FileHandle(outputPath.toString());
    	PixmapIO.writeCIM(output, pixmap);
	}

	public enum ConvertDirection {
    	TO_CIM("png", "cim", CimConverter::convertToCim),
		FROM_CIM("cim", "png", CimConverter::convertFromCim);

    	private final String sourceExtension, outputExtension;
    	final BiConsumer<Path, Path> converter;
    	ConvertDirection(String sourceExtension, String outputExtension, BiConsumer<Path, Path> converter) {
    	    this.sourceExtension = sourceExtension;
    	    this.outputExtension = outputExtension;
    	    this.converter = converter;
		}
	}
}
