package MainLogic;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import MainLogic.SensorObject;


/**
 * Created by Puzer on 10/24/2015.
 */
public class SensorObjectSerializer {

    public byte[] Serialize(SensorObject data){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] typeBytes = IntToByteArray(data.getType());
        outputStream.write(typeBytes,0,4);

        byte[] xBytes = FloatToByteArray(data.getX());
        outputStream.write(xBytes,0,4);

        byte[] yBytes = FloatToByteArray(data.getY());
        outputStream.write(yBytes,0,4);

        byte[] zBytes = FloatToByteArray(data.getZ());
        outputStream.write(zBytes,0,4);

        byte[] timeBytes = IntToByteArray(data.getTime());
        outputStream.write(timeBytes,0,4);

        return outputStream.toByteArray();
    }

    private byte[] IntToByteArray(int val){

        return ByteBuffer.allocate(4).putInt(val).array();
    }

    private byte[] FloatToByteArray(float val){
        return ByteBuffer.allocate(4).putFloat(val).array();
    }
}
