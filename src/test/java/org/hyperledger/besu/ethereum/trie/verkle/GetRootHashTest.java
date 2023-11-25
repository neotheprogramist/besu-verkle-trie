/*
 * Copyright Besu Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */
package org.hyperledger.besu.ethereum.trie.verkle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.hyperledger.besu.ethereum.trie.verkle.factory.StoredNodeFactory;

import org.apache.tuweni.bytes.Bytes32;
import org.junit.jupiter.api.Test;

public class GetRootHashTest {
  @Test
  public void testOneValueSimple() {
    SimpleVerkleTrie<Bytes32, Bytes32> trie = new SimpleVerkleTrie<Bytes32, Bytes32>();
    Bytes32 key =
        Bytes32.fromHexString("0x00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff");
    Bytes32 value =
        Bytes32.fromHexString("0x1000000000000000000000000000000000000000000000000000000000000000");
    trie.put(key, value);

    trie.getRootHash();
    Bytes32 commitmentAfterFirstCall = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterSecondCall = trie.getRoot().getCommitment().orElseThrow();

    assertEquals(commitmentAfterFirstCall, commitmentAfterSecondCall);
  }

  @Test
  public void testTwoValuesAtSameStem() throws Exception {
    SimpleVerkleTrie<Bytes32, Bytes32> trie = new SimpleVerkleTrie<Bytes32, Bytes32>();
    Bytes32 key1 =
        Bytes32.fromHexString("0x00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff");
    Bytes32 value1 =
        Bytes32.fromHexString("0x1000000000000000000000000000000000000000000000000000000000000000");
    Bytes32 key2 =
        Bytes32.fromHexString("0x00112233445566778899aabbccddeeff00112233445566778899aabbccddee00");
    Bytes32 value2 =
        Bytes32.fromHexString("0x0100000000000000000000000000000000000000000000000000000000000000");
    trie.put(key1, value1);
    trie.put(key2, value2);
    Bytes32 originalCommitment = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterFirstCall = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterSecondCall = trie.getRoot().getCommitment().orElseThrow();

    assertNotEquals(originalCommitment, commitmentAfterFirstCall);
    assertEquals(commitmentAfterFirstCall, commitmentAfterSecondCall);
  }

  @Test
  public void testTwoValuesAtDifferentIndex() throws Exception {
    SimpleVerkleTrie<Bytes32, Bytes32> trie = new SimpleVerkleTrie<Bytes32, Bytes32>();
    Bytes32 key1 =
        Bytes32.fromHexString("0x00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff");
    Bytes32 value1 =
        Bytes32.fromHexString("0x1000000000000000000000000000000000000000000000000000000000000000");
    Bytes32 key2 =
        Bytes32.fromHexString("0xff112233445566778899aabbccddeeff00112233445566778899aabbccddee00");
    Bytes32 value2 =
        Bytes32.fromHexString("0x0100000000000000000000000000000000000000000000000000000000000000");
    trie.put(key1, value1);
    trie.put(key2, value2);

    Bytes32 originalCommitment = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterFirstCall = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterSecondCall = trie.getRoot().getCommitment().orElseThrow();

    assertNotEquals(originalCommitment, commitmentAfterFirstCall);
    assertEquals(commitmentAfterFirstCall, commitmentAfterSecondCall);
  }

  @Test
  public void testOneValueStored() {
    NodeUpdaterMock nodeUpdater = new NodeUpdaterMock();
    NodeLoaderMock nodeLoader = new NodeLoaderMock(nodeUpdater.storage);
    StoredNodeFactory<Bytes32> nodeFactory =
        new StoredNodeFactory<>(nodeLoader, value -> (Bytes32) value);
    StoredVerkleTrie<Bytes32, Bytes32> trie = new StoredVerkleTrie<Bytes32, Bytes32>(nodeFactory);
    Bytes32 key =
        Bytes32.fromHexString("0x00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff");
    Bytes32 value =
        Bytes32.fromHexString("0x1000000000000000000000000000000000000000000000000000000000000000");
    trie.put(key, value);

    Bytes32 originalCommitment = trie.getRoot().getCommitment().orElseThrow();
    trie.commit(nodeUpdater);
    Bytes32 commitmentAfterFirstCall = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterSecondCall = trie.getRoot().getCommitment().orElseThrow();

    assertNotEquals(originalCommitment, commitmentAfterFirstCall);
    assertEquals(commitmentAfterFirstCall, commitmentAfterSecondCall);
  }

  @Test
  public void testThreeValues() {
    SimpleVerkleTrie<Bytes32, Bytes32> trie = new SimpleVerkleTrie<Bytes32, Bytes32>();
    Bytes32 key =
        Bytes32.fromHexString("0x4020000000000000000000000000000000000000000000000000000000000000");
    Bytes32 value =
        Bytes32.fromHexString("0x0000000000000000000000000000000000000000000000000000000000000000");
    trie.put(key, value);
    Bytes32 key2 =
        Bytes32.fromHexString("0x4000000000000000000000000000000000000000000000000000000000000000");
    Bytes32 value2 =
        Bytes32.fromHexString("0x0000000000000000000000000000000000000000000000000000000000000000");
    Bytes32 key3 =
        Bytes32.fromHexString("0x0000000000000000000000000000000000000000000000000000000000000000");
    Bytes32 value3 =
        Bytes32.fromHexString("0x0000000000000000000000000000000000000000000000000000000000000000");
    trie.put(key, value);
    trie.put(key2, value2);
    trie.put(key3, value3);

    Bytes32 originalCommitment = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterFirstCall = trie.getRoot().getCommitment().orElseThrow();
    trie.getRootHash();
    Bytes32 commitmentAfterSecondCall = trie.getRoot().getCommitment().orElseThrow();

    assertNotEquals(originalCommitment, commitmentAfterFirstCall);
    assertEquals(commitmentAfterFirstCall, commitmentAfterSecondCall);
  }
}
