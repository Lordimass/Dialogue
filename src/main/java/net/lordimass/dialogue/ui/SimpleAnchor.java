package net.lordimass.dialogue.ui;

import au.ellie.hyui.builders.HyUIAnchor;
import lombok.Setter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.jspecify.annotations.Nullable;

public class SimpleAnchor {
    @Nullable @Setter public Integer left;
    @Nullable @Setter public Integer right;
    @Nullable @Setter public Integer top;
    @Nullable @Setter public Integer bottom;
    @Nullable @Setter public Integer height;
    @Nullable @Setter public Integer full;
    @Nullable @Setter public Integer horizontal;
    @Nullable @Setter public Integer vertical;
    @Nullable @Setter public Integer width;
    @Nullable @Setter public Integer minWidth;
    @Nullable @Setter public Integer maxWidth;

    public SimpleAnchor() {}

    public SimpleAnchor(HyUIAnchor anchor) {
        this(anchor.toBsonDocument());
    }

    public SimpleAnchor(BsonDocument anchor) {
        this.left = getBsonInteger(anchor,"Left");
        this.right = getBsonInteger(anchor, "Right");
        this.top = getBsonInteger(anchor, "Top");
        this.bottom = getBsonInteger(anchor, "Bottom");
        this.height = getBsonInteger(anchor, "Height");
        this.full = getBsonInteger(anchor, "Full");
        this.horizontal = getBsonInteger(anchor, "Horizontal");
        this.vertical = getBsonInteger(anchor, "Vertical");
        this.width = getBsonInteger(anchor, "Width");
        this.minWidth = getBsonInteger(anchor, "MinWidth");
        this.maxWidth = getBsonInteger(anchor, "MaxWidth");
    }

    private Integer getBsonInteger(BsonDocument bson, String key) {
        BsonValue bsonValue = bson.get(key);
        if (bsonValue == null) return null;
        else return bsonValue.asInt32().getValue();
    }

    public SimpleAnchor add(SimpleAnchor anchor) {
        this.left += anchor.left;
        this.right += anchor.right;
        this.top += anchor.top;
        this.bottom += anchor.bottom;
        this.height += anchor.height;
        this.full += anchor.full;
        this.horizontal += anchor.horizontal;
        this.vertical += anchor.vertical;
        this.width += anchor.width;
        this.minWidth += anchor.minWidth;
        this.maxWidth += anchor.maxWidth;
        return this;
    }

    public SimpleAnchor clone() {
        SimpleAnchor anchor = new SimpleAnchor();
        anchor.left = this.left;
        anchor.right = this.right;
        anchor.top = this.top;
        anchor.bottom = this.bottom;
        anchor.horizontal = this.horizontal;
        anchor.vertical = this.vertical;
        anchor.width = this.width;
        anchor.minWidth = this.minWidth;
        anchor.maxWidth = this.maxWidth;
        return anchor;
    }

    public static class AnchorTemplateKeys {
        @Nullable @Setter public String left;
        @Nullable @Setter public String right;
        @Nullable @Setter public String top;
        @Nullable @Setter public String bottom;
        @Nullable @Setter public String height;
        @Nullable @Setter public String full;
        @Nullable @Setter public String horizontal;
        @Nullable @Setter public String vertical;
        @Nullable @Setter public String width;
        @Nullable @Setter public String minWidth;
        @Nullable @Setter public String maxWidth;

        public AnchorTemplateKeys() {}
    }
}
