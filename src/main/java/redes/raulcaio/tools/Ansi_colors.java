package redes.raulcaio.tools;

public class Ansi_colors {

    public static void main(String[] args) {
    }

    public String getColor(String color) {
        switch (color) {
            case "yellow":
                return "\u001B[33m";
            case "cyan":
                return "\u001B[36m";
            case "blue":
                return "\u001B[34m";
            case "green":
                return "\u001B[32m";
            case "red":
                return "\u001B[31m";
            case "purple":
                return "\u001B[32m";
            case "default":
                return "\u001B[0m";
            default:
                return "\u001B[0m";
        }
    }
}
