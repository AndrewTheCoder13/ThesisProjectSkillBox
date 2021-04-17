package main.service;

import main.model.User;
import main.repository.UserRepository;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Base64;
import java.util.Random;

@Service
public class ImageService {

    private UserRepository userRepository;

    private RandomGenerator randomGenerator;

    @Autowired
    public ImageService(UserRepository userRepository, RandomGenerator randomGenerator) {
        this.userRepository = userRepository;
        this.randomGenerator = randomGenerator;
    }

    public String image(MultipartFile file, boolean resize, Principal principal) throws IOException {
        String format = file.getContentType().substring(file.getContentType().indexOf("/") + 1);
        String path = randomGenerator.generate(11);
        File image = getImageFile(format, path);
        file.transferTo(Paths.get(image.getPath()));
        if (resize) {
            return resize(image, format, principal);
        }
        image.createNewFile();
        String newPath = image.getCanonicalPath().substring(image.getCanonicalPath().indexOf("upload") - 1).replaceAll("\\\\", "/");
        return newPath;
    }

    private File getImageFile(String format, String path){
        File fileParent = new File("upload");
        File firstFolder = new File(fileParent, path.substring(0, 2));
        firstFolder.mkdir();
        File secondFolder = new File(firstFolder, path.substring(2, 4));
        secondFolder.mkdir();
        File thirdFolder = new File(secondFolder, path.substring(4, 6));
        thirdFolder.mkdir();
        File image = new File(thirdFolder, path.substring(6) + "." + format);
        return image;
    }

    private String resize(File image, String format, Principal principal) throws IOException {
        BufferedImage img = ImageIO.read(image);
        img = scaleImage(img, 100);
        ImageIO.write(img, format, image);
        image.createNewFile();
        User user = userRepository.findByEmail(principal.getName()).get();
        String newPath = image.getCanonicalPath().substring(image.getCanonicalPath().indexOf("upload") - 1).replaceAll("\\\\", "/");
        user.setPhoto(newPath);
        userRepository.save(user);
        return newPath;
    }

    private BufferedImage scaleImage(BufferedImage image, int size) throws IOException {
        MarvinImage image2 = new MarvinImage(image);
        Scale scale = new Scale();
        scale.load();
        scale.setAttribute("newWidth", size);
        scale.setAttribute("newHeight", size);
        scale.process(image2.clone(), image2, null, null, false);
        return image2.getBufferedImageNoAlpha();
    }

    public BufferedImage resizeCaptchaImage(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(100, 35, BufferedImage.TYPE_INT_RGB);
        Graphics g = newImage.createGraphics();
        g.drawImage(image, 0, 0, 100, 35, null);
        g.dispose();
        return newImage;
    }

    public String formImageToString(BufferedImage image) {
        String encodedImage = "data:image/png;base64, ";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageInByte = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(imageInByte);
        encodedImage += encodedString;
        return encodedImage;
    }
}
