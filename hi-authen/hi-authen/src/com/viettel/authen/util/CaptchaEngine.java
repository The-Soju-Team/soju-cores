/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.util;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.NonLinearTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author HienDM
 */
public class CaptchaEngine extends ListImageCaptchaEngine
{
  protected void buildInitialFactories()
  {
    WordGenerator wordGenerator = new RandomWordGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
    
    TextPaster textPaster = new NonLinearTextPaster(4, 4, new RandomListColorGenerator(new Color[] { Color.BLACK }), Boolean.TRUE);
    
    BackgroundGenerator backgroundGenerator = new GradientBackgroundGenerator(150, 35, Color.WHITE, Color.WHITE);
    
    Font[] fontList = new Font[1];
    fontList[0] = new Font("Tahoma", Font.BOLD, 27);
    
    FontGenerator fontGenerator = new RandomFontGenerator(27, 27, fontList);
    
    WordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
    
    addFactory(new GimpyFactory(wordGenerator, wordToImage));
  }
}
