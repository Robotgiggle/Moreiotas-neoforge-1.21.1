package ram.talia.moreiotas.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.ArrayUtils;
import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MoreIotasCodecs {
    public static final Codec<SimpleMatrix> SIMPLEMATRIX = Codec.DOUBLE.listOf().listOf().xmap(list -> (double[][]) list.stream().map(a -> ArrayUtils.toPrimitive(a.toArray())).toArray(), arr -> {
        Stream<double[]> stream = Arrays.stream(arr);
        List<List<Double>> doubles = stream.map(a -> Arrays.stream(ArrayUtils.toObject(a)).toList()).toList();
        return doubles;
    }).xmap(SimpleMatrix::new, SimpleMatrix::toArray2);
    public static final StreamCodec<ByteBuf, SimpleMatrix> SIMPLEMATRIX_STREAM = new StreamCodec<ByteBuf, SimpleMatrix>() {
        @Override
        public SimpleMatrix decode(ByteBuf buffer) {
            int rows = buffer.readInt();
            int cols = buffer.readInt();
            double[][] arr = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    arr[i][j] = buffer.readDouble();
                }
            }

            return new SimpleMatrix(arr);
        }

        @Override
        public void encode(ByteBuf buffer, SimpleMatrix matrix) {
            buffer.writeInt(matrix.getNumRows());
            buffer.writeInt(matrix.getNumCols());
            int size = matrix.getNumRows() * matrix.getNumCols();
            double[] data = matrix.getDDRM().getData();
            for (int i = 0; i < size; i++) {
                buffer.writeDouble(data[i]);

            }
        }
    };
}
