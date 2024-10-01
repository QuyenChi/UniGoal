package src;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

class ThiSinh {
    private String hoVaTen;
    private String diaChi;
    private KetQua ketQua;

    public ThiSinh(String hoVaTen, String diaChi, KetQua ketQua) {
        this.hoVaTen = hoVaTen;
        this.diaChi = diaChi;
        this.ketQua = ketQua;
    }

    public KetQua getKetQua() {
        return ketQua;
    }
}

class KetQua {
    private String hinhThucThi;  // Ví dụ: "THPTQG"
    private Map<String, Double> diemThanhPhan;  // Map lưu điểm của từng môn

    public KetQua(String hinhThucThi, Map<String, Double> diemThanhPhan) {
        this.hinhThucThi = hinhThucThi;
        this.diemThanhPhan = diemThanhPhan;
    }

    public Map<String, Double> getDiemThanhPhan() {
        return diemThanhPhan;
    }
}

class TinhDiemCong {
    private ThiSinh thiSinh;
    private Map<String, Double> diemCongTheoVung;  // Điểm cộng theo vùng
    private Map<String, Double> diemCongKhac;      // Điểm cộng theo các tiêu chí khác (đối tượng ưu tiên)

    public TinhDiemCong(ThiSinh thiSinh) {
        this.thiSinh = thiSinh;
        this.diemCongTheoVung = taoDiemCongTheoVung();
        this.diemCongKhac = taoDiemCongKhac();
    }

    // Tạo bảng điểm cộng theo vùng (VD: KV1: 0.75, KV2: 0.5, KV3: 0)
    private Map<String, Double> taoDiemCongTheoVung() {
        Map<String, Double> diemCongVung = new HashMap<>();
        diemCongVung.put("KV1", 0.75);  // Khu vực 1
        diemCongVung.put("KV2", 0.5);   // Khu vực 2
        diemCongVung.put("KV2-NT", 0.25); // Khu vực 2 nông thôn
        diemCongVung.put("KV3", 0.0);   // Khu vực 3 không được cộng
        return diemCongVung;
    }

    // Tạo bảng điểm cộng theo các yếu tố khác (VD: diện ưu tiên 1, ưu tiên 2)
    private Map<String, Double> taoDiemCongKhac() {
        Map<String, Double> diemCongKhac = new HashMap<>();
        diemCongKhac.put("UT1", 2.0);  // Ưu tiên 1 (Dân tộc thiểu số, vùng đặc biệt khó khăn)
        diemCongKhac.put("UT2", 1.0);  // Ưu tiên 2
        return diemCongKhac;
    }

    // Hàm tính điểm cộng dựa trên khu vực của thí sinh
    public double tinhDiemCongVung(String khuVuc) {
        return diemCongTheoVung.getOrDefault(khuVuc, 0.0);  // Nếu không có khu vực thì không được cộng điểm
    }

    // Hàm tính điểm cộng theo các yếu tố khác như đối tượng ưu tiên
    public double tinhDiemCongKhac(String doiTuong) {
        return diemCongKhac.getOrDefault(doiTuong, 0.0);  // Nếu không có đối tượng ưu tiên thì không được cộng điểm
    }

    // Hàm tổng hợp điểm cộng
    public double tinhTongDiemCong(String khuVuc, String doiTuong) {
        double diemCongVung = tinhDiemCongVung(khuVuc);
        double diemCongKhac = tinhDiemCongKhac(doiTuong);
        return diemCongVung + diemCongKhac;
    }
}


class ToHop {
    private String tenToHop;  // Ví dụ: "A00"
    private List<String> monThuocTH;  // Danh sách các môn trong tổ hợp
    private Map<String, Double> trongSo;  // Trọng số cho từng môn

    public ToHop(String tenToHop, List<String> monThuocTH, Map<String, Double> trongSo) {
        this.tenToHop = tenToHop;
        this.monThuocTH = monThuocTH;
        this.trongSo = (trongSo != null) ? trongSo : getTrongSoMacDinh();
    }

    // Trọng số mặc định là 1 nếu không có
    private Map<String, Double> getTrongSoMacDinh() {
        return monThuocTH.stream().collect(java.util.stream.Collectors.toMap(mon -> mon, mon -> 1.0));
    }

    public List<String> getMonThuocTH() {
        return monThuocTH;
    }

    public Map<String, Double> getTrongSo() {
        return trongSo;
    }
}
class TinhKetQua {
    private ThiSinh thiSinh;
    private ToHop toHop;
    private TinhDiemCong tinhDiemCong;

    public TinhKetQua(ThiSinh thiSinh, ToHop toHop, TinhDiemCong tinhDiemCong) {
        this.thiSinh = thiSinh;
        this.toHop = toHop;
        this.tinhDiemCong = tinhDiemCong;
    }

    // Tính điểm tổng cho tổ hợp bao gồm cả điểm cộng
    public double tinhKQTH(String khuVuc, String doiTuong) {
        double tongDiem = 0.0;
        Map<String, Double> diemThanhPhan = thiSinh.getKetQua().getDiemThanhPhan();

        for (String mon : toHop.getMonThuocTH()) {
            double diem = diemThanhPhan.getOrDefault(mon, 0.0);
            double trongSoMon = toHop.getTrongSo().getOrDefault(mon, 1.0);
            tongDiem += diem * trongSoMon;
        }

        // Cộng thêm điểm ưu tiên
        double diemCong = tinhDiemCong.tinhTongDiemCong(khuVuc, doiTuong);
        tongDiem += diemCong;

        return tongDiem;
    }
}


class DaiHoc {
    private String tenTruong;
    private String diaChiTruong;
    private List<ChuyenNganh> cacCN;

    public DaiHoc(String tenTruong, String diaChiTruong, List<ChuyenNganh> cacCN) {
        this.tenTruong = tenTruong;
        this.diaChiTruong = diaChiTruong;
        this.cacCN = cacCN;
    }

    public List<ChuyenNganh> getCN() {
        return cacCN;
    }

    public String getTenTruong() {
        return tenTruong;
    }
}

class ChuyenNganh {
    private String tenChuyenNganh;
    private double diemSan;
    private List<ToHop> toHopXetTuyen;

    public ChuyenNganh(String tenChuyenNganh, double diemSan, List<ToHop> toHopXetTuyen) {
        this.tenChuyenNganh = tenChuyenNganh;
        this.diemSan = diemSan;
        this.toHopXetTuyen = toHopXetTuyen;
    }

    public double getDiemSan() {
        return diemSan;
    }

    public List<ToHop> getTHXT() {
        return toHopXetTuyen;
    }

    public String getTenChuyenNganh() {
        return tenChuyenNganh;
    }
}
class deXuat {
    private ThiSinh thiSinh;
    private List<DaiHoc> DHDuDK;

    public deXuat(ThiSinh thiSinh, List<DaiHoc> DHDuDK) {
        this.thiSinh = thiSinh;
        this.DHDuDK = DHDuDK;
    }

    public List<String> deXuatCN() {
        List<String> cacDeXuat = new ArrayList<>();

        for (DaiHoc daiHoc : DHDuDK) {
            for (ChuyenNganh chuyenNganh : daiHoc.getCN()) {
                for (ToHop toHop : chuyenNganh.getTHXT()) {
                    TinhKetQua calculator = new TinhKetQua(ThiSinh  , ToHop);
                    double score = calculator.tinhKQTH();

                    if (score >= chuyenNganh.getDiemSan()) {
                        cacDeXuat.add(String.format("Trường: %s, Ngành: %s, Điểm của bạn: %.2f, Điểm chuẩn: %.2f",
                                daiHoc.getTenTruong(), chuyenNganh.getTenChuyenNganh(), score, chuyenNganh.getDiemSan()));
                    }
                }
            }
        }

        return cacDeXuat;
    }
}


