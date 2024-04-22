#!/bin/bash
# clear

if [  $# -lt 1 ] ; then 
	echo " Error!!"
	echo
	echo " You should provide a secret name."
	echo " For example, if peekapoo is the secret name:"
	echo
        echo " bash /home/ad.ilstu.edu/cli2/Public/IT328/dfa/submitdfa.sh peekapoo"
	echo
	exit 
fi 

term=24S
itcourse=IT328
what=dfa
log=/home/ad.ilstu.edu/cli2/Public/IT328dock/Log_$what.$term

date >> $log
echo "$USER [$1] " >> $log
echo >> $log
echo >> $log

dir=$HOME/$itcourse/$what
if [ ! -d $dir ] ; then 
	echo  " Error!!"
	echo " You haven't created directory $dir for all of your programs."
	echo
	exit 
fi 
dock=/home/ad.ilstu.edu/cli2/Public/IT328dock/$USER
if [ ! -d $dock ] ; then 
	mkdir $dock
	chmod 733 $dock 
fi 
if [ ! -d $dock/$1 ] ; then
	mkdir $dock/$1	
	chmod 777 $dock/$1 
fi

if [ ! -d $dock/$1/$what ] ; then
	mkdir $dock/$1/$what
	chmod 777 $dock/$1/$what  
fi

# For DFA assignments 2 and 3 
if [ ! -f $HOME/$itcourse/npc/NFA2DFA.java ] ; then 
	echo
	echo "  NFA2DFA.java does not exist in $itcourse/dfa"
	echo
fi 
cp -r $dir/*.java $dock/$1/$what

chmod -R 777 $dock/$1/$what
ls -l $dock/$1/$what
echo "copy files into $dock/$1/$what"
echo
ls $dock/$1/$asg
echo 
echo "Only Java programs will be copied."

#chmod 755 $HOME
#chmod -R 700 $HOME/$itcourse


