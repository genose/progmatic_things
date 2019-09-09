package org.genose.java.implementation.net;

public class GNSObjectSSHFileTransfert extends GNSObjectSSHConnection {




    public boolean execFTPPut(String aLocalFilePath){

        try{
            super.open();

        }catch (Exception evErrPut){

        }

        return false;
    }

    public boolean execFTPCHDIR(String sFTPPath)
    {
        try{
            super.open();

        }catch (Exception evErrPut){

        }

        return false;

    }
}
