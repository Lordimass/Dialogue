import json
import os
import shutil

INPUT_RELATIVE_TO = "./main/resources/Common/Sounds"
OUTPUT_RELATIVE_TO = "./main/resources/Server/Audio/SoundEvents/SFX"
SUB_DIRECTORY = "Voice"
INPUT_DIR = INPUT_RELATIVE_TO + "/" + SUB_DIRECTORY
OUTPUT_DIR = OUTPUT_RELATIVE_TO + "/" + SUB_DIRECTORY


SOUND_EVENT_TEMPLATE = {
    "Layers": [
        {
            "Files": [], # Paths to assets go here.
            "RandomSettings": {
                "MinPitch": 0,
                "MaxPitch": 0,
                "MinVolume": 0
            },
            "Volume": 1.0
        }
    ],
    "Volume": 0,
    "MaxDistance": 70,
    "StartAttenuationDistance": 25
}

if __name__ == "__main__":

    # Clear output directory first
    for filename in os.listdir(OUTPUT_DIR):
        file_path = os.path.join(OUTPUT_DIR, filename)
        try:
            if os.path.isfile(file_path) or os.path.islink(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path):
                shutil.rmtree(file_path)
        except Exception as e:
            print('Failed to delete %s. Reason: %s' % (file_path, e))

    # Create SoundEvent JSONs
    for root, dirs, files in os.walk(INPUT_DIR):
        if len(files) == 0:
            continue
        local_path = (root[len(INPUT_RELATIVE_TO)+1:]+"/").replace("\\", "/")
        print(local_path)
        for file in files:
            new_filepath = local_path + file[0:-4] + ".json"
            data = SOUND_EVENT_TEMPLATE
            data["Layers"][0]["Files"] = ["Sounds/" + local_path+file]
            print(OUTPUT_RELATIVE_TO + "/" + new_filepath)
            os.makedirs(os.path.dirname(OUTPUT_RELATIVE_TO + "/" + new_filepath), exist_ok=True)
            with open(OUTPUT_RELATIVE_TO + "/" + new_filepath, "w") as f:
                json.dump(data, f, indent=4)
        print(root, dirs, files)

    # with open(OUTPUT_DIR+"/f1/a.json", "w") as file:
    #     json.dump(data, file, indent=4)