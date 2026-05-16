package net.queensfall.demo;

import lombok.Setter;

@Setter
public class DemoClass {

    private String customData = "Hello, World!";

    public final String getCustomData() {
        return customData;
    }

}
