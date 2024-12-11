package io.broderamera;

import java.awt.image.BufferedImage;
import java.io.InputStream;
// External
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

/**
 * Class for handling SVG images and converting them to BufferedImages. 
 * 
 * Inspired by :
 * @link https://web.archive.org/web/20131215231214/http://bbgen.net/blog/2011/06/java-svg-to-bufferedimage/
 * @author Olofur
 */
public class ImageManager extends ImageTranscoder {
    private BufferedImage image = null;

    /**
     * Creates a BufferedImage with the given width and height
     */
    @Override
    public BufferedImage createImage(int width, int height) {
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return buffer;
    }

    /**
     * Writes an image to the BufferedImage
     */
    @Override
    public void writeImage(BufferedImage image, TranscoderOutput to) throws TranscoderException {
        this.image = image;
    }

    /**
     * Returns an image from the resources folder
     * @param name
     * @return BufferedImage
     */
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
