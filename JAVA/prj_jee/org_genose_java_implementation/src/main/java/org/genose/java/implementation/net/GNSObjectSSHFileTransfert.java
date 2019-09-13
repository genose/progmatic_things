package org.genose.java.implementation.net;

import com.jcraft.jsch.Session;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

public class GNSObjectSSHFileTransfert extends GNSObjectSSHConnection {

// https://mydayswithlinux.wordpress.com/2015/09/03/mounting-remote-file-system-using-sshfs/
    // sshfs kris@192.168.2.10:/home/kris/merapi ~/ijen/
// https://stackoverflow.com/questions/24845401/mount-remote-disk-with-java-via-ssh


    public boolean open()
    {

        try {
            super.openSession();

        } catch (Exception evErrPut) {

        }

        return false;
    }
    public boolean get(String aLocalFileName, String sDistFileName) {

        try {
            super.openSession();

        } catch (Exception evErrPut) {

        }

        return false;
    }

    public boolean put(String aLocalFileName, String sDistFileName) {

        try {
            super.openSession();

        } catch (Exception evErrPut) {

        }

        return false;
    }

    public boolean chdir(String sFTPPath) {
        try {
            super.openSession();

        } catch (Exception evErrPut) {

        }

        return false;

    }
}
