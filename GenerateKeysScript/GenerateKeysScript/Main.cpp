#include <iostream>
#include <string>
#include <fstream>
#include <cstdlib>
#include <ctime>
#include <iomanip>

int main() {
	std::string str;
	double num;
	std::getline(std::cin, str);
	srand(time(NULL));
	for (int i = 0; i < atoi(str.c_str()); i++) {
		std::cout << std::fixed;
		std::cout << std::setprecision(5) << (double)(rand()) / RAND_MAX;
		if (i != atoi(str.c_str()) - 1) {
			std::cout << "\n";
		}
	}
	return 0;
}