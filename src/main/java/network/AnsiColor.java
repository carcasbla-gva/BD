package network;

public enum AnsiColor {
    BLACK("\u001B[30m"),
    WHITE("\u001B[37m"),
    BLUE("\u001B[38;2;40;177;249m"),
    RED("\u001B[38;2;255;100;100m"),
    RESET("\u001B[0m");

    private final String ansicolor;

    AnsiColor(String code){
        this.ansicolor = code;
    }
    public String getCode(){
        return this.ansicolor;
    }
}
