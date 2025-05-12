import cv2
import numpy as np

from process_phoneTookImg import find_docs, scale_small
from hough_lines_detect import hough_lines_detect, draw_table_line
from general_processing import adaptive_binarize
from crop_cell_image import get_cell_images

def decode(png_bytearray):
    np_arr = np.frombuffer(png_bytearray, np.uint8)
    return cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
def encode(numpy_image):
    # 將處理後影像編碼回位元組 (PNG格式)
    success, buf = cv2.imencode(".png", numpy_image) # 將回傳的np.array圖片編碼成Byte陣列
    if not success:
        return None
    return bytes(buf)  # 回傳處理後的影像位元組

# 已測試
def locate_template_image(png_bytearray):
    # 定位手機拍攝的空白文件圖片，自動計算完整文件的形狀
    # 參數：
    # png_bytearray: png編碼成byte陣列的圖片
    # 返回：定位後的png編碼圖片
    img = decode(png_bytearray)
    # img = scale_small(img) # 縮小圖片可加快運算速度(測試時因為表格座標寫死所以這裡先註解)
    result_img = find_docs(img, 'auto') # 原程式的定位文件函式
    return encode(result_img)

# 可能不會用到
def locate_target_image(png_bytearray, template_width, template_height):
    # 定位手機拍攝的目標辨識文件圖片，以空白文件(定位後)的形狀作為目標形狀
    # 參數：
    # png_bytearray: png編碼成byte陣列的圖片
    # template_width: 空白文件(定位後)的寬
    # template_height: 空白文件(定位後)的高
    # 返回：定位後的png編碼圖片
    img = decode(png_bytearray)
    result_img = find_docs(img, (template_height, template_width)) # 原程式的定位文件函式
    return encode(result_img)


from general_processing import adaptive_binarize
# 已測試
def detect_lines(png_bytearray, focus_coord):
    # 先用座標(表格範圍)切割圖片，再將切割後的圖片(白底黑線)送往直線檢測
    # 參數：
    # png_bytearray: png編碼成byte陣列的圖片
    # focus_coord: 由x1,x2, y1,y2組成的陣列，代表框選圖片的的左(x1)、右(x2)、上(y1)、下(y2)
    # 返回：二維陣列，分別為row表格線座標和column表格線座標
    img = decode(png_bytearray)
    x1, x2, y1, y2 = focus_coord
    img = img[y1:y2,x1:x2]
    template_height, template_width = img.shape[:2]
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    img = adaptive_binarize(img)
    row_border_array, col_border_array = hough_lines_detect(img, inv=True, min_gap=min(template_height, template_width)*0.05)
    return row_border_array.tolist(), col_border_array.tolist()

def crop_image(png_bytearray, x1,x2, y1,y2):
    # 給一張圖片，返回用作標切割出的圖片
    # 參數：
    # png_bytearray: png編碼成byte陣列的圖片
    # x1,x2, y1,y2: 切割的左(x1)、右(x2)、上(y1)、下(y2)
    # 返回：切割後的編碼圖片
    img = decode(png_bytearray)
    result_img = img[y1:y2, x1:x2]
    return encode(result_img)

# 未測試
def crop_cell_images(target_byte_array, template_byte_array, col_lines, row_lines, model_input_size):
    # 給一張目標辨識文件圖片(定位前)、表格線的欄座標和列座標、模板形狀、和模型輸入大小，輸出所有準備送給模型辨識的欄位圖片。
    # 參數：
    # png_bytearray: png編碼成byte陣列的圖片
    # cols, rows: 表格線的欄座標和列座標
    # complete_doc_shape: 模板(定位後)形狀，用於模型定位後要調整成的形狀
    # model_input_size: 模型的圖片輸入大小
    # 返回：一系列的切割並處理的欄位圖片
    img = decode(target_byte_array)
    template = decode(template_byte_array)
    located_img = find_docs(img, template.shape)

    cropped_images = get_cell_images(located_img, row_lines, col_lines, 'auto')
    cropped_images = [cv2.resize(image, (model_input_size, model_input_size)) for image in cropped_images]
    cropped_encoded_images = [encode(image) for image in cropped_images]

    return cropped_encoded_images

# 已測試
def draw_table_lines(png_bytearray, rows, cols):
    # 給一張文件圖片(定位後)、表格線的欄座標和列座標，在圖片上面畫上偵測到的表格線。
    # 參數：
    # png_bytearray: png編碼成byte陣列的圖片
    # cols, rows: 霍夫直線偵測到表格線的欄座標和列座標
    # 返回：畫了偵測到的表格線的圖片
    img = decode(png_bytearray)
    draw_table_line(img, rows, cols)
    return encode(img)

