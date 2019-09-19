package org.genose.java.implementation.net;

import org.genose.java.implementation.files.GNSObjectMappedFileTypeDelimitedSeparator;
import org.genose.java.implementation.files.GNSObjectMappedFileTypeINI;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

import java.nio.file.Path;
import java.util.Properties;

public class GNSObjectMappedParamater extends Properties {

    public GNSObjectMappedParamater()
    {
        super();
    }

    public GNSObjectMappedParamater(Path aFileParameter){


    }

    public GNSObjectMappedParamater(GNSObjectMappedFileTypeDelimitedSeparator mappedFileTypeDelimitedSeparator)
    {

    }

    public GNSObjectMappedParamater(GNSObjectMappedFileTypeINI mappedFileTypeINI){

    }

}
