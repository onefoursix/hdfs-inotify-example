# hdfs-inotify-example

HDFS iNotify example

See http://www.slideshare.net/Hadoop_Summit/keep-me-in-the-loop-inotify-in-hdfs for info on iNotify, 
particularly slide #16

You must run this tool as the hdfs user.

    Usage: $ java -jar hdfs-inotify-example-uber.jar <HDFS URI>  [<TxId>]

This is a quick and dirty example.  If you omit the TxId arg,like this:

    $ sudo -u hdfs java -jar hdfs-inotify-example-uber.jar hdfs://brooklyn.onefoursix.com:8020
    
... the output might be quite verbose, as you will get all tx's

So you might want to start with a large TxId and then work backwards if you don't get any events.

(If the TxId is larger than the number of tx's then you will simply get no data back)

For my test on a new HDFS I will start will a TxId of 0:

    $ sudo -u hdfs java -jar hdfs-inotify-example-uber.jar hdfs://brooklyn.onefoursix.com:8020 0

I see output that ends like this:

    ...
    TxId = 351352
    event type = CREATE
      path = /tmp/.cloudera_health_monitoring_canary_files/.canary_file_2015_07_10-20_29_11
      owner = hdfs
      ctime = 1436585351213

ctrl-c to kill the app

From that you can see the last TxId was 351352

You can then call the app like this to get all subsequent tx's:

    $ sudo -u hdfs java -jar hdfs-inotify-example-uber.jar hdfs://brooklyn.onefoursix.com:8020 351352

While that is still running, in another session, create a couple of files in HDFS, then delete one (without using -skipTrash) and delete the other with -skipTrash.

You should see a couple of CREATE events, a RENAME and an UNLINK, like this:
    
    TxId = 351411
    event type = CREATE
      path = /user/mark/data106.txt._COPYING_
      owner = mark
      ctime = 1436585999907
    TxId = 351412
    event type = CLOSE
    TxId = 351413
    event type = RENAME
      src = /user/mark/data106.txt._COPYING_
      dst = /user/mark/data106.txt
      timestamp = 1436586000137
    TxId = 351420
    event type = RENAME
      src = /user/mark/data106.txt
      dst = /user/mark/.Trash/Current/user/mark/data106.txt
      timestamp = 1436586013973
    TxId = 351421
    event type = CREATE
      path = /user/mark/data107.txt._COPYING_
      owner = mark
      ctime = 1436586067256
    TxId = 351425
    event type = CLOSE
    TxId = 351426
    event type = RENAME
      src = /user/mark/data107.txt._COPYING_
      dst = /user/mark/data107.txt
      timestamp = 1436586067489
    TxId = 351427
    event type = UNLINK
      path = /user/mark/data107.txt
      timestamp = 1436586074079

