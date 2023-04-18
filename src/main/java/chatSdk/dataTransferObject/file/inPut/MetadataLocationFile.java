package chatSdk.dataTransferObject.file.inPut;

import chatSdk.dataTransferObject.map.inPut.MapLocation;

public class MetadataLocationFile extends MetaDataImageFile {

    private MapLocation location;


    public MapLocation getLocation() {
        return location;
    }

    public void setLocation(MapLocation location) {
        this.location = location;
    }
}
