// #include <mbed.h>
// #include <APA102.h>
//
// APA102 matrix = APA102(p23, p24, p25, 200000);
//
// int main() {
//     unsigned int data[256] = {0};
//     for (unsigned char i = 0; i < 255; i++) {
//         data[i] = 0xEF00FF00;
//     }
//     matrix.SetBuffer(data, 8, 32, 0, 0, true, false);
//     matrix.Repaint();
//     wait_ms(3000);
//     while (1) {
//         for (unsigned char i = 0; i < 255; i++) {
//             data[1] = i << 8;
//             matrix.SetBuffer(data, 8, 32, 0, 0, true, false);
//             matrix.Repaint();
//             wait_ms(10);
//         }
//     }
// }
