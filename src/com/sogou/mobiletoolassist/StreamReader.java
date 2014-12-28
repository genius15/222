package com.sogou.mobiletoolassist;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
public class StreamReader extends Thread {
    private InputStream is;
    private StringBuffer mBuffer;
    private String mCharset;
    private CountDownLatch mCountDownLatch;

    public StreamReader(InputStream is, String charset) {
        this.is = is;
        mCharset = charset;
        mBuffer = new StringBuffer("");
        mCountDownLatch = new CountDownLatch(1);
    }

    public String getResult() {
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mBuffer.toString();
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(is, mCharset);
            int c = -1;
            while ((c = isr.read()) != -1) {
                mBuffer.append((char) c);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null)
                    isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }
    }
}

