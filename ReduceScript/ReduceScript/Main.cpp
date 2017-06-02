#include <iostream>
#include <string>
#include <fstream>
#include <ctime>
#include <cstdlib>

int main() {
	std::string key, val, num;
	int sum = 0, col, i;
	double end;
	srand(time(NULL));
	std::getline(std::cin, num);
	std::getline(std::cin, key, '\t');
	std::getline(std::cin, val);
	for (i = 1; i <= atoi(num.c_str()); i++) {
		if (atof(key.c_str()) <= (double)i / atoi(num.c_str())) {
			sum += stoi(val);
			break;
		}
	}
	end = (double)i / atoi(num.c_str());
	while (std::getline(std::cin, key, '\t') && std::getline(std::cin, val)) {
		if (atof(key.c_str()) <= end) {
			sum += stoi(val);
		}
		else {
			std::cout << end << "\t" << sum << '\n';
			i++;
			end = (double)i / atoi(num.c_str());
			sum = 0;
			break;
		}
	}
	std::cout << end << "\t" << sum << '\n';
	return 0;
}