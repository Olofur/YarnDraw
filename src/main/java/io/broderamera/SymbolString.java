package io.broderamera;

import java.awt.*;

public class SymbolString {
    private Image symbol;
    private String symbolName;

    public SymbolString(Image symbol, String symbolName) {
        this.symbol = symbol;
        this.symbolName = symbolName;
    }

    public void setSymbolString(Image symbol, String symbolName) {
        this.symbol = symbol;
        this.symbolName = symbolName;
    }

    public Image getImage() {
        return symbol;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public boolean hasSymbol(Image symbol) {
        return this.symbol.equals(symbol);
    }

    public boolean hasSymbolName(String symbolName) {
        return this.symbolName.equals(symbolName);
    }
}
