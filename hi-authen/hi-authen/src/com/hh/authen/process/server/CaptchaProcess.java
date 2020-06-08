/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.authen.process.server;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.authen.run.ServerProcess;
import com.hh.authen.util.CaptchaEngine;
import com.hh.connector.server.Server;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.Gimpy;
import io.netty.channel.ChannelHandlerContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author HienDM
 */
public class CaptchaProcess extends ServerProcess {
    
    public CaptchaProcess(ChannelHandlerContext ctx, Server server) {
        super(ctx, server);
    }
    
    @Override
    public void process(LinkedTreeMap msg) throws Exception {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        CaptchaEngine bge = new CaptchaEngine();

        ImageCaptchaFactory factory = bge.getImageCaptchaFactory();
        ImageCaptcha pixCaptcha = factory.getImageCaptcha();
        setSessionAttribute(msg,"sso_captcha",((Gimpy)pixCaptcha).response.toLowerCase());

        BufferedImage challenge = pixCaptcha.getImageChallenge();
        ImageIO.write(challenge, "jpeg", jpegOutputStream);
        byte[] captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        String encodedfile = new String(Base64.encodeBase64(captchaChallengeAsJpeg), "UTF-8");
        LinkedTreeMap result = new LinkedTreeMap();
        result.put("data", encodedfile);
        returnStringToFrontend(msg, new Gson().toJson(result));
    }
    
}
