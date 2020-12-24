package main.api.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String secret;
    private String image;
}
