package org.example;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ResourceTest {
    public static void main(String[] args) {
        String imagePath = "/images/lose.jpg";

        // Test Method 1
        URL url1 = ResourceTest.class.getResource(imagePath);
        System.out.println("Method 1 URL: " + url1);

        // Test Method 2
        URL url2 = ResourceTest.class.getClassLoader().getResource("images/lose.jpg");
        System.out.println("Method 2 URL: " + url2);

        try {
            if (url2 != null) {
                BufferedImage img = ImageIO.read(url2);
                System.out.println("Image loaded successfully. Size: " + img.getWidth() + "x" + img.getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
