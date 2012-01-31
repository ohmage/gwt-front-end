#!/bin/bash

# Helper script to deploy and restart app on multiple servers.
# Run from project root dir.

## print current branch
#echo `git branch`
#
## get latest
#echo `git pull`
#
## compile
#echo `ant gwtc`
#
## generate Mobilize.war
#echo `ant buildwar`
#
# name of file copied to server will be Mobilize.war.mm-dd-yy
DATE_STRING=`date +%m-%d-%y`
REMOTE_FILENAME=MobilizeWeb.war.$DATE_STRING

# which servers the war should be deployed to
SERVERS=(webadmin@dev.andwellness.org)

# for each server:
# - copy war into temp dir (which must already exist) 
# - stop app server 
# - remove old files
# - copy new war into webapps 
# - restart app server. 
# (note: it's assumed id_rsa.pub key allows passwordless login)
for SERVER in ${SERVERS[@]}
do
  echo "......................................"
  echo "deploying $REMOTE_FILENAME to $SERVER"
  echo `scp MobilizeWeb.war $SERVER:~/temp/$REMOTE_FILENAME`
  echo "stopping app server on $SERVER";
  echo `ssh $SERVER "/opt/aw/as/bin/asd.sh stop"`
  echo "removing old MobilizeWeb directory"
  echo `ssh $SERVER "rm -rf /opt/aw/as/webapps/ROOT"`
  echo "copying $SERVER:$REMOTE_FILENAME to $SERVER:/opt/aw/as/webapps/ROOT.war" 
  echo `ssh $SERVER "cp ~/temp/$REMOTE_FILENAME /opt/aw/as/webapps/ROOT.war"`
  echo "restarting app server on $SERVER"
  echo `ssh $SERVER "/opt/aw/as/bin/asd.sh start"`
  echo "done deploying $REMOTE_FILENAME to $SERVER"
  echo ""
  echo ""
done

