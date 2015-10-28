package com.arsdigita.mimetypes.image;

import com.arsdigita.util.Dimension;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class ImageIOSizer extends ImageSizer {
	
	private static final Logger s_log = Logger.getLogger(ImageIOSizer.class);

	protected ImageIOSizer() {
		super();
	}

	public Dimension computeSize(DataInputStream in) throws IOException {
		try {
			BufferedImage image = ImageIO.read(in);
			return new Dimension(image.getWidth(), image.getHeight());
		}
		catch (IOException e) {
			throw e;
		}
		catch (Throwable t) {
			s_log.warn("computeSize", t);
		}
		return null;
	}

}
