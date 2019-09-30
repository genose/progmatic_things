package org.genose.java.implementation.graphics;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GNSObjectGraphics {
    /*
    https://javahotfix.blogspot.com/2019/01/using-graphics-magick-with-java-to.html
    https://sourceforge.net/projects/jmagick/
    http://im4java.sourceforge.net/
    http://www.graphicsmagick.org/download.html
    https://github.com/techblue/jmagick


    https://www.codota.com/code/java/classes/magick.MagickImage

     */
    public void resizeImage(String originalFile,
                            String targetFile,
                            int width,
                            int height) throws InterruptedException, IOException, IM4JavaException {
        ConvertCmd command = new ConvertCmd(true);
        IMOperation operation = new IMOperation();

        operation.addImage(originalFile);
        operation.resize(width, height);
        operation.addImage(targetFile);

        // Execute the Operation
        command.run(operation);
    }

    public void performMultipleOperation(
            InputStream input,
            OutputStream output) throws IOException, InterruptedException, IM4JavaException {
        ConvertCmd command = new ConvertCmd(true);
        Pipe pipeIn = new Pipe(input, null);
        Pipe pipeOut = new Pipe(null, output);

        command.setInputProvider(pipeIn);
        command.setOutputConsumer(pipeOut);

        IMOperation operation = new IMOperation();
        operation.addImage("-");
        operation.blur(100.0);  // Radius of Blur effect
        operation.border(15,15); // width height parameter
        operation.addImage("-");

        command.run(operation);
    }
}
