package com.finalprojectdaar.searchengine;


import com.finalprojectdaar.searchengine.algorithmes.RegexToDfa;
import static org.assertj.core.api.Assertions.assertThat;

import com.finalprojectdaar.searchengine.algorithmes.State;
import com.finalprojectdaar.searchengine.algorithmes.SyntaxTree;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SearchengineApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testRegex() throws IOException {

		RegexToDfa regex = new RegexToDfa();
		State q0 = regex.buildDfa("Bill#");
		boolean result = regex.findMatch(q0, 2);
		assertThat(result).isEqualTo(true);
	}

	@Test
	void onePlusOne() {
		int a = 1+1;
		assertThat(a).isEqualTo(2);
	}

}
