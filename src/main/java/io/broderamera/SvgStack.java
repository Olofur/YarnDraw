package io.broderamera;

import java.util.ArrayList;

public class SvgStack {
    public ArrayList<String> stack;

    public SvgStack() {
        stack =  new ArrayList<>();
    }

    public void addItem(String item) {
        stack.add(item);
    }

    public String popItem() {
        return stack.remove(stack.size() - 1);
    }

    public void main(String[] args) {
        new SvgStack();
    }
}