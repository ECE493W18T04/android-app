#include <mbed.h>
#include <APA102.h>

// APA102 matrix = APA102(p23, p24, p25, 2000000);
SPI _spi = SPI(p23, p24, p25);

int main() {
    unsigned int data[256] = {0};
    _spi.format(8,3);
    _spi.frequency(2000000);
    for (int i = 0; i < 256; i++) {
        data[i] = 0xE0000000;
    }
    // matrix.SetBuffer(data, 8, 32, 8, 0, true, false);
    // matrix.Repaint();
    while (1) {
        for (uint16_t i = 0; i < 256; i++) {
            data[i] = 0xEF0000FF;
	    _spi.write(0X00);  // Start
	    _spi.write(0X00);
	    _spi.write(0X00);
	    _spi.write(0X00);
	    
	    for(int index = 0; index < 256; index++)
	    {
		unsigned int val = data[index];
		_spi.write((val>>24)&0xFF);  
		_spi.write((val>>16)&0xFF);  
		_spi.write((val>>8)&0xFF);  
		_spi.write(val&0xFF);  
	    }
	    _spi.write(0XFF); // Stop
	    _spi.write(0XFF);
	    _spi.write(0XFF);
	    _spi.write(0XFF);
            wait_ms(100);
        }
    }
}
