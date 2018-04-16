package me.wener.jraphql.schema;

import com.github.wenerme.wava.util.JSON;
import com.google.common.base.Suppliers;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import lombok.SneakyThrows;
import me.wener.jraphql.lang.Document;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2018/4/3
 */
public class MetaSchema {
  private static final String BASE64 =
      "H4sIAAAAAAAAAM1dbXPiOBL+Kyq+3G4VIdiYt7lP7ITMcpshu3nZq6vduSCMCN4xsscvYait/Pdr\n"
          + "SbaxSUiwZIurrdoJ2LhbT7e6W61W++/GQ7T1SeND48Kz4zWhUaPZCL04sMmVZ+PI8Wjjw98N16Fw\n"
          + "T7vZsD03XlP+p7gLfhnENHLWpPHcbCzI0qEO+1XY+PDH39nDb23s4uAOPlxkd7xByHyd0F8B9lff\n"
          + "3PM1iXDrUXwIGVnbWzPWizQ/ii/fIGOUJkMj9sQPDXS3Img2odEMhXxoiBFFAfEDEjJOEPXo2TLA\n"
          + "NqOIXRQ6j5Qs0GbluQRRADpwbPSE3ZiELQTPQTamu58nV9CcRBtCKDr7wfxvx/gRYbpA7C90hoxW\n"
          + "4/lLs0HxmrEMTwAGpfHu6sHbUsT70vXwYcQTiBdePHfJGXxtOyGQRzkpJLBiuNmHy0sH7p9v0R+T\n"
          + "8XiM+l3ryw+rKPI/nJ8T2to4Xx2fLBzc8oLHc/bpnN32sGRMOPTxwfccGv1YEANnUEUQAz2C6CsK\n"
          + "4jYKAIGDkojI9ygGuBc4ws3dBQAbkL+/uzwbIHuFmVhIgELyLSbUZvOAPVs8WjzSCdHaCyPkLYE6\n"
          + "ikMhrU+Msd+uUOTlJswyIORs6QVrtIrXmJ4FBC8w6AHnpSAjQUBFSIahyTy1FcX0kwfWBtODcppF\n"
          + "QUxmyAvQbIndkMwKQCW/VkLK0oRUR9WSXxwECaOYOqCiyFnAZ2YzgmZeI7kaLklkr8A8I2/+F7Ej\n"
          + "Bimo+leyRUv2J1h3e0WEgk8uBAns+wQHIXIoXP/X7fUUHhP64DgJ+ylOJsI/0crbkCdG04nYhKAe\n"
          + "/MOYXwjac7Kn8S307xXwRr6DhUumHPDlUD+OOOEmfNyiUEyzH8KY8Q2q8GfD+rMx+5Fxzh7/CDNz\n"
          + "d9GCC+IJ3IKijeO6jDK2beLviEwuik7poqA7Fw6Y5Mh5OlJ1+ppUpyevOmJEYO9ArOQ7seMI0AOZ\n"
          + "ONR24wXYnhUIDBTGXTBYwQ89MmaQR90tRAIgJPbDmbOcIRw88uCLSZhNygKOyeNgEG7CPhtkYzK9\n"
          + "mkzHD5c3o0+fx9M7uJz++XD76814dMG+mYyvLhrwqJTAxYEAbcKk+zsT7nHSGe5Qs+qUzqA0nUw6\n"
          + "EwHbIoH6BapLoCsY2XE09eg0dl1m4I4c/eAYrl6QARYWVRPZN9vPz1/kp5/Z0TP9TLP66RdC4HZo\n"
          + "7pWYduwx/09zzuzqmXOmJT/nbgEzv44plx98bVOuNJFqp9xQ05QbyE+5zzj4yl09cYnwZUsIVdKI\n"
          + "PIQgZ41ZMEA95HqUBRFh7PteADFCQRMWLL4Cxshif4aNp/efH34fXd2P06n0cDG+nEwnd5PraQ2z\n"
          + "qtPRM6s6bflZNf7uu9ihIUyrrTBsKfwbwHqHZRMiUlh3QXQB0XwahrAwD4MYHh9JyFnKa+gRfBvy\n"
          + "fLPYF8JXZpNZpBiGO22A1fraYeE2WyO20CWs3HDEvndKM2jKM/gH0+eFt6Fi4R/Cyn+BWWC8hJk7\n"
          + "ByRblETnfuCxoD48Xyd3nxdX/RB5h1zHZGxOXv8Ms4TRydaxLOmGY1fofJ68uEN8fRwDpnEMA0+C\n"
          + "EFjuF5O8sWcBr/ly6Oj1oqUp8ddRCPpHKDPrCDTjCZaFbLm2wVum5/DBDhy2NHIjElCYlShJjyYh\n"
          + "CstIsTQeXwMCks6C8warqRV+cvjqK2dRF0lStlV2UshneMpSUvAmE4pCD5CxcUjCJtp6MaJELGsT\n"
          + "ZJHncwvPTQhDNEXmH2EOzxS7sqwP5VkHKYHE0wgz9UmhWBmzNXoYL5eODavtdBENP144Ig8JBnpn\n"
          + "m0uzbSmkhVhM6wuPwDlv7ZQ5zDDnHma+TXWZ375IeQfOl9xYl3cllkK+HeSfD/ML9vfhIRsEUOfj\n"
          + "OhQcXBYvvsVs2bggYYb/Ixvl5oka7brC3PJU9lzOc17uJSC15CAVeugnT5cacY4ySyyVH7HcgLty\n"
          + "A94FxNKKlKNsdGUU6coJo9qJSAymV9usKE3lpfnJnvr8LD1JenI6Ay5IQV16qnbnGHVRJiIxGKM2\n"
          + "dSlNJVOX3QpV6EkhYh7TeH38vq2mnShrWE28zDa5WWy8+AvbbPkKXn3NdiN8HEDs5C25j09jXxfT\n"
          + "xxg/sg2LkoFGVyFCemU2Z0E9sEiJCOh8Lwwdtr+YjMV2SHgoKMkYbDYISHcvN1GUxfjlDW8NVFPy\n"
          + "oquwxs5QzEsdo28xCbYQ3pOAXy1g99v9+OY/jf1ZUQIWTZnSrlU5LOs4El+/jszn+7sRT4QpgNPX\n"
          + "BE6vcnDCeJ5FggcAur3/6fbjzeRXVZA07W91B5WDJNZ1haoUviEhD0bP0ANGr109GOm+z64wrQhN\n"
          + "unuTyzErAKXJHPfM+oAKfVZI8DpIyRaXAkCaDHOvcsPMKijYszOgipUOe9uCCghpss69Gqyz2P05\n"
          + "MNNuP/48/jxSQUaTSe4NakCGVxYdRGZ0NbpRQKavyT7329XPqqReimfDD+Bz/dO/xh9V5lRfk1nu\n"
          + "m/U484POa393VAEiTYa5X4Nhzoo4DsA0uvl0X52P72sy0P1eHS4sIsESvjkE1WR6N765HH0cqwCk\n"
          + "yU73q7fTMRWr/VexuZ+qKc5Ak5Ue1GClWdriEC6sYEMFFk3GeWDWBIuoiH0DnKSaRQEiTcZ5UEvU\n"
          + "zKqGj/Dyk+mv93cPyr5+oMk8D3o1Y/Wm5xdgvfT/KrUXQ00Vn0OF5Ow1LMCyzKuYeKK+/tF5IhQx\n"
          + "VWnx/2cnfQKSlu+Lb5p8c75sQnmosHPtuyDjlecuSJAwm9Tew9+Fg2At9LMo9We6sBsEq00NSBQH\n"
          + "VKY6aqhQX/vqsQTB/F6qO5uj1e2/D60T7L/nida3/16eSjX778Puqfbf85Q17r8PJfdSnfAiX58q\n"
          + "qUv5DcLaSpbLU3lZsyyHbV9WmQSy8LAblbrJPH12WKG8Sqk4S6Ot64hyW8VdilCClRtOsqUfIy0c\n"
          + "Y7qtyU9VYuQ6/Kgl4joAjpJge8U+b1YO/LHCYUnXY7QNFd/DxNUEb8++cURRc8ZiVm/X5IPDiYPk\n"
          + "Y9tzTXw01bklo32KurAC1fockwSZajyT0T5ZaViBtEbfZLS7Jyj0KVCtrdKnAioywzHqmxfdyop9\n"
          + "JJVFMpKJxMBk0e3psTo9Savz8MAfLY9q/4TxYYF6fQGiBJmKIkSjPThtiFhg4AQxorbuB4ZCoDXK\n"
          + "ji4k5f/8+IWIAVmKInc+gB9bWZEkT7VMboForGxSwjBMlUMjQD2NbAOy316EX0a/i/SPCF2zMznA\n"
          + "vJM0eICxlGZaocmEOMaSRLHJea0k61MMV3PuorqY1eieImY1ulq8R3kyFcWsRu9kMWuetM6Y1eif\n"
          + "IgzJU61TkfonCkO0NdNQ6aYxSuuyz5bZWd0kkZ1ZYN64aUWKBo4nvfmRslwHmb3D4Lnzq7LCG0pG\n"
          + "OdW4elNTBzVDpV9GJkJ0myvVIqKBho19PHdcGCkJ9071k+CJBC00iVhTIS9kkYDrlvWepoL3xE/Y\n"
          + "cUU3L5G8gkBksYtLPNHRQ/DZZEHAhrgu+5cfGaRRsEW8WVvIdLE04wpd63iRezMr6hbZqtermPdP\n"
          + "DwgRVRgEmJp2Rg2zp2JlshQfSFBIe9czYJ50P0g0Mg8Yv1XexeTBkcsjHJMWUaciMxyzNo9ZnswL\n"
          + "jynrM01dPtNU8Jms3RtfbUQrHO2feAmzLmqB5/HFS7GmmN9+pxQ45UGSPKB5lBqUJlNR4NTRVFJl\n"
          + "qDRvmSzzRiu1ZmHOKUQFNXl5Aug9TUl/cUhZjpFhHspS8VMmQ0kRdnSJ0KxahAVHvi/FA07+HUnm\n"
          + "f6UkzXxPF1OjNLu6pGlVFWPk4sijAo3d/fJ2uaN8oP2YaEOdisxw+rW5mY5sEUm+P8jLA9dll3pW\n"
          + "T1dP24FaU9tlTBeYEcEuq4ETGk+3+4tAJ8xMF29QGxCeVuXnsr86lKd4y66ZVDrriKDfoRmfOCzk\n"
          + "ePncJGgmTNUvwOGMF+KWbZFkKPTRKU1KIUF/QXxCRacgsc5lQhErdNHO1yZBhAGvJCGfZbtzjXoQ\n"
          + "nnus/e9qrzv5MaybaqJsodtdX+VdjyHqFfibk63HCznYlBXre5LzrZuV45LSnCtkHHj5ZZFjsXmQ\n"
          + "tux/o7Qm94NEJqU5V0g5ANWscxOAP5qHEWu0LnhronuaNiDL2G4WOL7elWqX57urUCabNVSI0o5p\n"
          + "LcTcHGc28UQJwmBRWR4KecBwolh72ZPEl1SVO7Ek895sqsqHCZaevLellvdmFlh+CWcNqt2aOmrA\n"
          + "A9UdI8mxDk+2YZQnrXPDqNuWG3JiN6vvW55nqFQlT9KE/e3Cj2NEkeeg05apxzjc8TO55b2Wn0Wx\n"
          + "tEv0/OSviXhmplZmRVIgO6hxRVIgNKzNcpYns1eWKp//7BqSpUup139t+XqUDA1Nq8oCofpWleXJ\n"
          + "vMxhS4rQlBNhGindHdjyOEqKOdrGsE4p5giZ9cUw5clUJkXJevCsE1gtXq4jmZGvzsvlOOhYJ/Jy\n"
          + "edFY+rxcjmzXrHNu5Ql16ptbpcm8ci5QYYJZsp4O5sxlGkdKCdJS9kBHCtLSUglcnsyhKnZJSUqW\n"
          + "IXpLlb2QPFW5TWr5/pxGV9M7PYyuQrJ4lDV4yPp9b9geVppoFAWzyRnrJO86Q85rOZdfRMKj4jaX\n"
          + "hq4OdIZKC7oJXTjMZ4Zi7yh9g2Ha/qnank+Grl5zhkqzuUOQpH2fWmgmVvoznuWb7VYnM74Jwd+P\n"
          + "kCRRq+0JZehqRWeo9KI7jF+G1D6EhdXBOyhW0e7H0NWxzlBpWXdwbvKWP60XsPFrOdwq7QRk6Gpl\n"
          + "Z/QGdege321Ds90K5h28FDsEGbr62xn9dj1TddfWpcVsXBaXvoNbRS1xDF3974x+Da6CF2sAbCII\n"
          + "fAexq8mtGlK6nEK/BqfAXzlOYblxLFrT6+nD9P7qSq1vkDFoa4px+yovroNrv/ECzPSlvY8kYnug\n"
          + "gcdeBS7avOOINcL11kndT9ntz75C1QP/2vnmigKxDQ5oWuUwQ34AcvheDLaz8VS4yynRIk3ypa4K\n"
          + "75T7BGKz4yDgvYpFjXwBmDCtm5dci+dBMKza1uLlyeyfDJBehku0eZMUc0dezJcOLwfhxm2+5WUh\n"
          + "RTknR8qqzpzmwSm1P6h2VrJAtlef1lmS77jZnZX8UgFps6evJFVXQ0BDoktjpu03on1NatcYUV4I\n"
          + "5VDE7/peumxLpT/hZ+ywN5Qvk1fBi9om+MDXSi9noaLm9/Vofl9d8+G//wGRx18nL4cAAA==";

  private static final Supplier<Document> DOCUMENT_SUPPLIER =
      Suppliers.memoize(MetaSchema::parseDoc);

  public static Document getDocument() {
    return DOCUMENT_SUPPLIER.get();
  }

  @SneakyThrows
  private static Document parseDoc() {
    byte[] bytes = Base64.getMimeDecoder().decode(BASE64);
    GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(bytes));
    return JSON.parse(ByteStreams.toByteArray(stream), Document.class);
  }
}
