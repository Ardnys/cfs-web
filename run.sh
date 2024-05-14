# virtual environment path
venv_path=~/.virtualenvs/g4f

# check if venv exists
if [ ! -d "$venv_path" ]; then
    echo "Virtual environment doesn't exist. Creating..."

    # create venv
    python -m venv "$venv_path"
    echo "Virtual environment created."
fi

# activate it
source ~/.virtualenvs/g4f/Scripts/activate

# install dependencies
pip install -r requirements.txt

# run demon samurai
python samurai.py &

# build the ktor server
./gradlew build

# locate the jar
jar_path=./build/libs/ktor-supabase-all.jar

# MAKE SURE YOUR JAVA_HOME IS A JDK 17
# run the jar
java -jar "$jar_path"

# deactivate venv
deactivate