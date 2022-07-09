package cc.hicore.Utils;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;

public class MediaUtils {
    static MediaExtractor mExtractor;

    static MediaCodec mDecoder;
    public synchronized static void MP3ToPCM(String Source,String dest) {
        try{
            mExtractor = new MediaExtractor();
            mExtractor.setDataSource(Source);
            mExtractor.selectTrack(0);
            MediaFormat inputFormat = mExtractor.getTrackFormat(0);


            if(!inputFormat.getString(MediaFormat.KEY_MIME).startsWith("audio"))return;

            mDecoder = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME));

            mDecoder.configure(inputFormat,null,null,0);
            mDecoder.start();

            FileOutputStream fOut = new FileOutputStream(dest);
            BufferedOutputStream buffWrite = new BufferedOutputStream(fOut);

            //编码过程

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo(); //缓冲区
            info.size = 4096*16; //设置output buffer 的大小
            while (true) {
                int inIndex = mDecoder.dequeueInputBuffer(5000);
                if (inIndex >= 0) {
                    ByteBuffer buffer =  mDecoder.getInputBuffer(inIndex);
                    //从MediaExtractor中读取一帧待解的数据
                    int sampleSize = mExtractor.readSampleData(buffer, 0);
                    //小于0 代表所有数据已读取完成
                    if (sampleSize < 0) {
                        mDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        //插入一帧待解码的数据
                        mDecoder.queueInputBuffer(inIndex, 0, sampleSize, mExtractor.getSampleTime(), 0);
                        //MediaExtractor移动到下一取样处
                        mExtractor.advance();
                    }
                }
                int outIndex = mDecoder.dequeueOutputBuffer(info, 5000);
                switch (outIndex) {
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        //  MediaFormat format = mDecoder.getOutputFormat();
                        //  audioTrack.setPlaybackRate(format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        break;
                    default:
                        ByteBuffer outBuffer = mDecoder.getOutputBuffer(outIndex);
                        //BufferInfo内定义了此数据块的大小
                        final byte[] chunk = new byte[info.size];
                        //  createFileWithByte(chunk);
                        //将Buffer内的数据取出到字节数组中
                        outBuffer.get(chunk);
                        //数据取出后一定记得清空此Buffer MediaCodec是循环使用这些Buffer的，不清空下次会得到同样的数据
                        outBuffer.clear();
                        try {
                            //TODO 在这里处理解码后的数据
                            //将解码出来的PCM数据IO流存入本地文件。
                            //fos.write(chunk);
                            int vnum;
                            if (chunk.length % (4096*16) == 0) {

                                vnum = chunk.length / (4096*16);
                            } else {
                                vnum = chunk.length / (4096*16) + 1;
                            }
                            byte[] bytes = new byte[4096*16];
                            for (int v = 0; v < vnum; v++) {
                                if (v != vnum - 1) {
                                    //1 初始数据 2  从元数据的起始位置开始 3 目标数组 4 目标数组的开始起始位置 5  要copy的数组的长度
                                    System.arraycopy(chunk, v * (4096*16), bytes, 0, (4096*16));
                                    buffWrite.write(bytes);
                                } else {
                                    System.arraycopy(chunk, v * 4096, bytes, 0, chunk.length - v * (4096*16));
                                    buffWrite.write(bytes,0,chunk.length - v * (4096*16));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //此操作一定要做，不然MediaCodec用完所有的Buffer后 将不能向外输出数据

                        mDecoder.releaseOutputBuffer(outIndex, false);
                        break;
                }
                if (info.flags != 0) {
                    break;
                }
            }
            buffWrite.close();
        }catch (Exception e) {
            LogUtils.error("MediaToPCM",e);
        }
    }
    public static byte[] ConvertDataToSilk(byte[] b){
        try{
            Object wechatNsWrapper = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.SilkCodecWrapper"),
                    new Class[]{Context.class}, HookEnv.AppContext);

            Object audioCompositeProcessor = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.qqaudio.audioprocessor.AudioCompositeProcessor"));
            MMethod.CallMethod(audioCompositeProcessor,null,void.class,
                    new Class[]{MClass.loadClass("com.tencent.mobileqq.qqaudio.audioprocessor.IAudioProcessor")},
                    wechatNsWrapper);

            MMethod.CallMethod(audioCompositeProcessor,null,void.class,
                    new Class[]{int.class,int.class,int.class},
                    16000,16000,1);

            byte[] MixData = new byte[6400];
            int Mix = b.length;

            int Pos = 0;
            ByteArrayOutputStream outData = new ByteArrayOutputStream();
            outData.write(2);
            outData.write("#!SILK_V3".getBytes(StandardCharsets.UTF_8));
            while (Mix>0){
                int GetLength = Math.min(Mix, 6400);
                Mix -= GetLength;


                System.arraycopy(b,Pos,MixData,0,GetLength);
                Object ProcessData = MMethod.CallMethod(audioCompositeProcessor,null,MClass.loadClass("com.tencent.mobileqq.qqaudio.audioprocessor.IAudioProcessor$ProcessData"),new Class[]{
                        byte[].class,int.class,int.class
                },MixData,0,GetLength);

                Pos+=GetLength;
                if(ProcessData!=null)
                {
                    byte[] gProcessResult = MField.GetField(ProcessData,"c",byte[].class);
                    int length = MField.GetField(ProcessData,"a",int.class);

                    outData.write(Arrays.copyOf(gProcessResult,length));
                }
            }

            MMethod.CallMethod(audioCompositeProcessor,null,void.class,new Class[0]);
            return outData.toByteArray();
        }catch (Exception e){
            LogUtils.error("SilkCodec",e);
            return null;
        }
    }
}
