if [ -z "$1" ]; then
  echo "Need to specify version number"
  exit 1	
fi	

echo "Updating to version $1"
mvn versions:set -DnewVersion=$1
mvn versions:update-child-modules
mvn versions:commit
