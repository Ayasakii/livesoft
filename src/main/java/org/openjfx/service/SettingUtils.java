package org.openjfx.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjfx.domain.Audio;
import org.openjfx.domain.Video;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class SettingUtils {

    public void checkJsonFile() {
        File file = new File("videoSetting.json");
//找不到文件直接新建音频视频文件
        if (!file.exists()) {
            Video video = new Video();
            Audio audio = new Audio();
            // 文件不存在
            writeJsonFile(video,audio);
        }
    }


    public void writeJsonFile(Video video,Audio audio) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("videoSetting.json"), video);
            mapper.writeValue(new File("audioSetting.json"), audio);
            System.out.println("保存文件成功");

        } catch (Exception e) {
//            JsonGenerationException, JsonMappingException, IOException
            e.printStackTrace();
        }

    }


    public Video readVidioJSON() {
        Video video = new Video();
        try {
            ObjectMapper mapper = new ObjectMapper();
            video = mapper.readValue(new File("videoSetting.json"), Video.class);
//            重新设置视频名
            video.setFileName(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(LocalDateTime.now()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return video;
    }

    public Audio readAudioJSON() {
        Audio audio = new Audio();
        try {
            ObjectMapper mapper = new ObjectMapper();
            audio = mapper.readValue(new File("audioSetting.json"), Audio.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audio;
    }

}
