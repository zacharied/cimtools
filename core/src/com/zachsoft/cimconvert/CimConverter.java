package com.zachsoft.cimconvert;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import java.util.Stack;
import java.util.function.BiFunction;

public class CimConverter extends ApplicationAdapter {
    private List<Path> paths;
    private ConvertDirection direction;
    private boolean doPreview;

    private Stack<Texture> previewImages;
    private SpriteBatch batch;

    public CimConverter(List<Path> paths, ConvertDirection direction, boolean doPreview) {
        this.paths = paths;
        this.direction = direction;
        this.doPreview = doPreview;

        if (doPreview)
            previewImages = new Stack<>();
    }

    @Override
    public void create() {
        if (doPreview)
            batch = new SpriteBatch();

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
                return;
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

            FileHandle sourceHandle = new FileHandle(path.toString()),
                    outputHandle = new FileHandle(outputPath.toString());
            Pixmap pixmap = fileDirection.converter.apply(sourceHandle, outputHandle);

            if (doPreview)
                previewImages.push(new Texture(pixmap));

            System.out.printf("Converted '%s' to '%s'\n", path, outputPath);
        }

        if (!doPreview)
            Gdx.app.exit();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (previewImages != null && !previewImages.empty()) {
            batch.begin();
            batch.draw(previewImages.peek(), 0, 0);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                previewImages.pop();
        } else {
            Gdx.app.exit();
        }
    }

    public enum ConvertDirection {
        TO_CIM("png", "cim", (source, output) -> {
            Pixmap pixmap = new Pixmap(source);
            PixmapIO.writeCIM(output, pixmap);
            return pixmap;
        }),

        FROM_CIM("cim", "png", (source, output) -> {
            Pixmap pixmap = PixmapIO.readCIM(source);
            PixmapIO.writePNG(output, pixmap);
            return pixmap;
        });

        private final BiFunction<FileHandle, FileHandle, Pixmap> converter;
        private final String sourceExtension, outputExtension;

        ConvertDirection(String sourceExtension, String outputExtension, BiFunction<FileHandle, FileHandle, Pixmap> converter) {
            this.sourceExtension = sourceExtension;
            this.outputExtension = outputExtension;
            this.converter = converter;
        }
    }
}
