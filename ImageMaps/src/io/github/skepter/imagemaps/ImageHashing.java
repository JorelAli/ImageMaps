package io.github.skepter.imagemaps;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class ImageHashing {
	
	BufferedImage img;

	public ImageHashing(BufferedImage img) {
		this.img = img;
	}
	
	public String getHash() throws NoSuchAlgorithmException, IOException {
		Main.getInstance().getDataFolder().mkdirs();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);
		out.flush();
		MessageDigest digester = MessageDigest.getInstance("MD5");

		byte[] bytes = digester.digest(out.toByteArray());
		out.close();
		
		return DatatypeConverter.printHexBinary(bytes);
	}
}
