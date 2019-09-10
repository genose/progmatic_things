package org.genose.java.implementation.net;

import org.genose.java.implementation.files.GNSObjectMappedFileTypeDelimitedSeparator;
import org.genose.java.implementation.files.GNSObjectMappedFileTypeINI;

import java.nio.file.Path;
import java.util.Properties;

public class GNSObjectConnectionParamater extends Properties {

    public GNSObjectConnectionParamater()
    {
        super();
    }

    public GNSObjectConnectionParamater(Path aFileParameter){


    }

    public  GNSObjectConnectionParamater(GNSObjectMappedFileTypeDelimitedSeparator mappedFileTypeDelimitedSeparator)
    {

    }

    public GNSObjectConnectionParamater(GNSObjectMappedFileTypeINI mappedFileTypeINI){

    }
    
}
