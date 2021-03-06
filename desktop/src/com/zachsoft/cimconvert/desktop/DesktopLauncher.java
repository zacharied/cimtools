package com.zachsoft.cimconvert.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.zachsoft.cimconvert.CimConverter;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
@CommandLine.Command(
        name = "cimtools",
        footer = "If neither `-t` nor `-f` are given, the extension of the file will be used to decide which " +
                "direction to convert in.")
public class DesktopLauncher {
    @CommandLine.ArgGroup()
    private ConvertDirectionOption conversionDirection;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this and exit.")
    private boolean doHelp;

    @CommandLine.Option(names = {"-p", "--preview"}, description = "Show the converted texture in a window.")
    private boolean doPreview;

    @CommandLine.Parameters
    private List<Path> files;

    public static void main(String[] args) {
        // Parse arguments.
        CommandLine.ParseResult result = new CommandLine(new DesktopLauncher()).parseArgs(args);

        // Show help if necessary.
        if (result.isUsageHelpRequested()) {
            int code = CommandLine.executeHelpRequest(result);
            System.exit(code);
        }

        if (!result.hasMatchedPositional(0)) {
            System.err.println("At least one path is required.");
            System.exit(1);
        }

        List<Path> paths = result.matchedPositional(0).getValue();

        CimConverter.ConvertDirection direction =
                result.hasMatchedOption('t') ? CimConverter.ConvertDirection.TO_CIM :
                result.hasMatchedOption('f') ? CimConverter.ConvertDirection.FROM_CIM :
                null;

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(new CimConverter(paths, direction, result.hasMatchedOption('p')), config);
    }

    private static class ConvertDirectionOption {
        @CommandLine.Option(
                names = {"-t", "--to-cim"},
                description = "Force input file(s) to be treated as an image, converting them to a CIM.")
        private boolean toCim;

        @CommandLine.Option(
                names = {"-f", "--from-cim"},
                description = "Force input file(s) to be treated as a CIM, converting them to an image.")
        private boolean fromCim;
    }
}
