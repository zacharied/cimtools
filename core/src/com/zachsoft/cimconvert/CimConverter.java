package com.zachsoft.cimconvert;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CimConverter extends ApplicationAdapter {
	private List<Path> paths;

	private Pixmap pixmap;
    private Texture pixmapTexture;

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
		    if (path.endsWith(".cim")) {
				outputPath = Paths.get(path.toString().replaceAll("\\.cim$", ".png"));
				convertFromCim(path, outputPath);
			} else if (path.endsWith(".png")) {
				outputPath = Paths.get(path.toString().replaceAll("\\.png$", ".cim"));
				convertToCim(path, outputPath);
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
	    pixmapTexture.dispose();
	    pixmap.dispose();
	}

	private void convertFromCim(Path cimPath, Path outputPath) {
		FileHandle handle = new FileHandle(cimPath.toString());
		pixmap = PixmapIO.readCIM(handle);
		pixmapTexture = new Texture(pixmap, Pixmap.Format.RGB888, false);

		FileHandle output = new FileHandle(outputPath.toString());
		PixmapIO.writePNG(output, pixmap);
	}

	private void saveCim(String outputPath) {
	}
}
