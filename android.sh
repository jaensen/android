android list targets
if [ $1 ]; then
android update project -p . --subprojects -t $1
fi
