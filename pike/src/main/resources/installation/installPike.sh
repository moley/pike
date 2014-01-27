echo "Installing pike in `pwd`"

for i in `find tmp -name "*.ZIP"`
do
  echo "Found $i"
  unzip -o $i
done

export CURRENTGRADLE=`ls -d gradle*`
export CURRENTJDK=`ls -d jdk*`

echo "Current gradle version $CURRENTGRADLE"

echo "Current jdk version $CURRENTJDK"

mv $CURRENTGRADLE gradle

mv $CURRENTJDK jdk