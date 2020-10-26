package com.picturesque.randompictures.controller;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.ws.rs.client.Client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

// mark this class as a controller to handle / requests
@RestController
@RequestMapping(value = "/")
public class PicturesController {

    private static final Integer DEFAULT_IMAGE_SIZE = 300;

    @RequestMapping(method = RequestMethod.GET, produces = "image/jpeg")
    public @ResponseBody
    byte[] getImage() throws IOException {
        final Client client = ClientBuilder.newClient();
        String url = "https://picsum.photos/" + DEFAULT_IMAGE_SIZE.toString();

        WebTarget target = client.target(url);
        Response response = target
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE)
                .request(MediaType.MEDIA_TYPE_WILDCARD)
                .get();

        if (response.getStatus() != Response.Status.FOUND.getStatusCode())
            return null;

        URI location = response.getLocation();
        target = client.target(location);
        response = target.request(MediaType.MEDIA_TYPE_WILDCARD).get();
        ImageInputStream is = (ImageInputStream) ImageIO.createImageInputStream(response.getEntity());
        BufferedImage bi = ImageIO.read(is);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpeg", bao);
        InputStream fis = new ByteArrayInputStream(bao.toByteArray());
        response.close();
        return IOUtils.toByteArray(fis);
    }
}
