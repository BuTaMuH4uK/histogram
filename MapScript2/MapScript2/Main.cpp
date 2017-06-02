#include <iostream>
#include <string>
#include <fstream>

int main() {
	bool other = false;
	for (std::string str; std::getline(std::cin, str);) {
		for (int i = 0; i < str.length(); i++) {
			if (str[i] > 47 && str[i] < 58 || str[i] == 46) {
				std::cout << str[i];
				other = true;
			} else if (other) {
				std::cout << '\t' << "1" << '\n';
				other = false;
			}
		}
		if (other) {
			std::cout << '\t' << "1" << '\n';
			other = false;
		}
	}
	return 0;
}