package io.broderamera;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

/**
 * Inspired by
 * @link https://web.archive.org/web/20131215231214/http://bbgen.net/blog/2011/06/java-svg-to-bufferedimage/
 */
public class ImageManager extends ImageTranscoder {

    private BufferedImage image = null;

    @Override
    public BufferedImage createImage(int width, int height) {
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return buffer;
    }

    @Override
    public void writeImage(BufferedImage image, TranscoderOutput to) throws TranscoderException {
        this.image = image;
    }

    public BufferedImage getImage(String name) {
        InputStream file = getClass().getResourceAsStream("/" + name);
        TranscoderInput transcoderInput = new TranscoderInput(file);
        try {
            this.transcode(transcoderInput, null);
        } catch (TranscoderException e) {
            System.err.println("Error transcoding SVG: " + e.getMessage());
        }
        return image;
    }
}